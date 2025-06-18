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
