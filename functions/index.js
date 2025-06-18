const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notifyNewBloodRequest = onDocumentCreated(
    "bloodRequests/{requestId}",
    async (event) => {
      const data = event.data.data();
      const message = {
        notification: {
          title: "New Blood Request",
          body: "Blood Group " +
            data.bloodGroup +
            " requested at " +
            data.location,
        },
        topic: "blood_requests",
      };

      try {
        await admin.messaging().send(message);
      } catch (e) {
        console.error("Error sending FCM", e);
      }
    },
);

exports.notifyBloodRequestAccepted = onDocumentCreated(
    "acceptances/{acceptanceId}",
    async (event) => {
      const acceptance = event.data.data();
      const requestId = acceptance.requestId;

      // Fetch the original request to get requesterId
      const requestSnap = await admin.firestore()
          .collection("bloodRequests")
          .doc(requestId)
          .get();

      if (!requestSnap.exists) {
        console.error("Blood request not found:", requestId);
        return;
      }
      const request = requestSnap.data();
      const requesterId = request.requesterId;

      // Fetch requester's FCM token from users collection
      const userSnap = await admin.firestore()
          .collection("users")
          .doc(requesterId)
          .get();

      if (!userSnap.exists) {
        console.error("Requester user not found:", requesterId);
        return;
      }
      const user = userSnap.data();
      const fcmToken = user.fcmToken; // Make sure this is saved in Firestore

      if (!fcmToken) {
        console.error("No FCM token for user:", requesterId);
        return;
      }

      const message = {
        notification: {
          title: "Your blood request was accepted!",
          body: "A donor has accepted your request. " +
          "Please check the app for more details.",
        },
        token: fcmToken,
      };

      try {
        await admin.messaging().send(message);
      } catch (e) {
        console.error("Error sending FCM to requester", e);
      }
    },
);

