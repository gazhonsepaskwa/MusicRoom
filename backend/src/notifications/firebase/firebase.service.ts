import { Injectable, OnModuleInit } from '@nestjs/common';
import { initializeApp, cert, getApps } from 'firebase-admin/app';
import { getMessaging } from 'firebase-admin/messaging';

@Injectable()
export class FirebaseService implements OnModuleInit {
  onModuleInit() {
    if (!getApps().length) {
      initializeApp({
        credential: cert(require('/run/secrets/firebase_service_account.json')),
      });
    }
  }

  async sendPushNotification(deviceTokens: string[], title: string, body: string) {
    try {
      const response = await getMessaging().sendEachForMulticast({
        tokens: deviceTokens,
        notification: {
          title,
          body,
        },
      });

      return response;
    } catch (error) {
      console.error(error);
      throw error;
    }
  }
}
