#!/usr/bin/env python3

from requests import post, get
from subprocess import call, Popen, PIPE
from time import sleep
import os
import socket
import sqlite3
import hashlib
import base64

# Steps implemented in this test.
#
# 0 Prepare sandbox.
#  -> (a) Make a EBICS host, (b) make a EBICS subscriber
#     for the test runner, and (c) assign a IBAN to such
#     subscriber.
#
# 1 Prepare nexus.
#  -> (a) Make a Nexus user, (b) make a EBICS subscriber
#     associated to that user
#
# 2 Prepare the Ebics transport for the nexus user.
#  -> (a) Upload keys from Nexus to the Bank (INI & HIA),
#     (b) Download key from the Bank (HPB) to the Nexus,
#     and (c) Fetch the bank account owned by that subscriber
#     at the bank.

# 3 Request history from the Nexus to the Bank (C53).
# 4 Verify that history is empty.
# 5 Issue a payment from Nexus
#  -> (a) Prepare & (b) trigger CCT.
# 6 Request history again, from Nexus to Bank.
# 7 Verify that previous payment shows up.

# Nexus user details
USERNAME="person"
PASSWORD="y"
USER_AUTHORIZATION_HEADER = "basic {}".format(base64.b64encode(b"person:y").decode("utf-8"))

# Admin authentication
ADMIN_AUTHORIZATION_HEADER = "basic {}".format(base64.b64encode(b"admin:x").decode("utf-8"))

# EBICS details
EBICS_URL="http://localhost:5000/ebicsweb"
HOST_ID="HOST01"
PARTNER_ID="PARTNER1"
USER_ID="USER1"
EBICS_VERSION = "H004"

# Subscriber's bank account
SUBSCRIBER_IBAN="GB33BUKB20201555555555"
SUBSCRIBER_BIC="BUKBGB22"
SUBSCRIBER_NAME="Oliver Smith"
BANK_ACCOUNT_LABEL="savings"

def checkPorts(ports):
    for i in ports:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            s.bind(("0.0.0.0", i))
            s.close()
        except:
            print("Port {} is not available".format(i))
            exit(77)

def assertResponse(response):
    if response.status_code != 200:
        print("Test failed on URL: {}".format(response.url))
        # stdout/stderr from both services is A LOT of text.
        # Confusing to dump all that to console.
        print("Check nexus.log and sandbox.log, probably under /tmp")
        nexus.terminate()
        sandbox.terminate()
        exit(1)
    # Allows for finer grained checks.
    return response

#-1 Clean databases and start services.
os.chdir("..")
assert(0 == call(["rm", "-f", "sandbox/libeufin-sandbox.sqlite3"]))
assert(0 == call(["rm", "-f", "nexus/libeufin-nexus.sqlite3"]))
DEVNULL = open(os.devnull, "w")

# Start nexus
checkPorts([5001])
nexus = Popen(["./gradlew", "nexus:run"], stdout=PIPE, stderr=PIPE)
for i in range(10):
    try:
      get("http://localhost:5001/")
    except:
        if i == 9:
            nexus.terminate()
            stdout, stderr = nexus.communicate()
            print("Nexus timed out")
            print("{}\n{}".format(stdout.decode(), stderr.decode()))
            exit(77)
        sleep(2)
        continue
    break
# Start sandbox
checkPorts([5000])
sandbox = Popen(["./gradlew", "sandbox:run"], stdout=PIPE, stderr=PIPE)
for i in range(10):
    try:
      get("http://localhost:5000/")
    except:
        if i == 9:
            nexus.terminate()
            sandbox.terminate()
            stdout, stderr = nexus.communicate()
            print("Sandbox timed out")
            print("{}\n{}".format(stdout.decode(), stderr.decode()))
            exit(77)
        sleep(2)
        continue
    break

#0.a
assertResponse(
    post(
        "http://localhost:5000/admin/ebics-host",
        json=dict(
            hostID=HOST_ID,
        ebicsVersion=EBICS_VERSION
        )
    )
)

#0.b
assertResponse(
    post(
        "http://localhost:5000/admin/ebics-subscriber",
        json=dict(
            hostID=HOST_ID,
        partnerID=PARTNER_ID,
        userID=USER_ID
        )
    )
)

#0.c
assertResponse(
    post(
        "http://localhost:5000/admin/ebics-subscriber/bank-account",
        json=dict(
            subscriber=dict(
                hostID=HOST_ID,
                partnerID=PARTNER_ID,
                userID=USER_ID
        ),
            iban=SUBSCRIBER_IBAN,
            bic=SUBSCRIBER_BIC,
            name=SUBSCRIBER_NAME,
        label=BANK_ACCOUNT_LABEL
        )
    )
)

#1.a, make a new nexus user.

# "Create" the admin user first.
dbconn = sqlite3.connect("nexus/libeufin-nexus.sqlite3")
dbconn.execute(
    "INSERT INTO NexusUsers (id, password) VALUES (?, ?)",
    ("admin", sqlite3.Binary(hashlib.sha256(b"x").digest()))
)
dbconn.commit()
dbconn.close()

assertResponse(
    post(
        "http://localhost:5001/users",
        headers=dict(Authorization=ADMIN_AUTHORIZATION_HEADER),
        json=dict(
        username=USERNAME,
        password=PASSWORD
        )
    )
)

#1.b, make a ebics transport for the new user.
assertResponse(
    post(
        "http://localhost:5001/bank-transports",
        json=dict(
            transport=dict(
                name="my-ebics",
                type="ebics"
            ),
            data=dict(
                ebicsURL=EBICS_URL,
                hostID=HOST_ID,
                partnerID=PARTNER_ID,
                userID=USER_ID
            )
        ),
        headers=dict(Authorization=USER_AUTHORIZATION_HEADER)
    )
)

#2.a, upload keys to the bank (INI & HIA)
assertResponse(
    post(
        "http://localhost:5001/bank-transports/sendINI",
        json=dict(
          type="ebics",
          name="my-ebics"
        ),
        headers=dict(Authorization=USER_AUTHORIZATION_HEADER)
    )
)

assertResponse(
    post(
        "http://localhost:5001/bank-transports/sendHIA",
        json=dict(
          type="ebics",
          name="my-ebics"
        ),
        headers=dict(Authorization=USER_AUTHORIZATION_HEADER)
    )
)

#2.b, download keys from the bank (HPB)
assertResponse(
    post(
        "http://localhost:5001/bank-transports/syncHPB",
        json=dict(
          type="ebics",
          name="my-ebics"
        ),
        headers=dict(Authorization=USER_AUTHORIZATION_HEADER)
    )
)

#2.c, fetch bank account information
assertResponse(
    post(
        "http://localhost:5001/bank-transports/syncHTD",
        json=dict(
          type="ebics",
          name="my-ebics"
        ),
        headers=dict(Authorization=USER_AUTHORIZATION_HEADER)
    )
)

#3, request history to Nexus
assertResponse(
    post(
        "http://localhost:5001/bank-accounts/collected-transactions",
        json=dict(
          type="ebics",
          name="my-ebics"
        ),
        headers=dict(Authorization=USER_AUTHORIZATION_HEADER)
    )
)

#4, make sure history is empty
resp = assertResponse(
    get(
        "http://localhost:5001/bank-accounts/{}/collected-transactions".format(BANK_ACCOUNT_LABEL),
        headers=dict(Authorization=USER_AUTHORIZATION_HEADER)
    )
)
assert(len(resp.json().get("transactions")) == 0)

nexus.terminate()
sandbox.terminate()
exit(44)

#5.a
assertResponse(
    post(
        "http://localhost:5001/users/{}/prepare-payment".format(USERNAME),
        json=dict(
            creditorIban="FR7630006000011234567890189",
            creditorBic="AGRIFRPP",
            creditorName="Jacques La Fayette",
            debitorIban=SUBSCRIBER_IBAN,
            debitorBic=SUBSCRIBER_BIC,
            debitorName=SUBSCRIBER_NAME,
        subject="integration test",
        sum=1
        )
    )
)

#5.b
assertResponse(
    post("http://localhost:5001/ebics/execute-payments")
)

#6
assertResponse(
    post(
        "http://localhost:5001/ebics/subscribers/{}/collect-transactions-c53".format(USERNAME),
        json=dict()
    )
)

resp = assertResponse(
    get(
        "http://localhost:5001/users/{}/history".format(USERNAME)
    )
)
assert(len(resp.json().get("payments")) == 1)

nexus.terminate()
sandbox.terminate()
print("Test passed!")