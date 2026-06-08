import { Injectable } from '@nestjs/common';
import * as nodemailer from 'nodemailer';
import * as fs from "fs";

@Injectable()
export class MailService {
	private transporter = nodemailer.createTransport({
		host: process.env.SMTP_HOST,
		port: Number(process.env.SMTP_PORT),
		secure: true,
		auth: {
			user: process.env.SMTP_USER,
			pass: fs.readFileSync("/run/secrets/smtp_password", "utf8"),
		}
	});

	async sendVerificationEmail(emailAddress: string, verificationLink: string) {
		try {
			await this.transporter.verify();
			console.log("Server is ready to take our messages");
		}
		catch (err) {
			console.error("Verification failed:", err);
		}
		try {
			const info = await this.transporter.sendMail({
				from: `Music Room <${process.env.SMTP_FROM}>`,
				to: emailAddress,
				subject: "Confirm your address",
				html:	`<h1>Welcome!</h1>
						<p>Please verify your account:</p>
						<a href="${verificationLink}">
						Verify Account
						</a>`,
			});
		}
		catch (err) {
			console.error("Error while sending mail:", err);
		}
	}
}
