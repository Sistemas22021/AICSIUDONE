import { SNSEvent } from "aws-lambda";
import { sendEmail } from "./services/mailService";
import { getWelcomeEmailTemplate } from "./templates/welcomeTemplate";
import { getResetPasswordTemplate } from "./templates/resetPasswordTemplate";

export const handler = async (event: SNSEvent): Promise<void> => {
    try {
        for (const record of event.Records) {
            const snsMessage = record.Sns.Message;
            console.log("Raw SNS Message:", snsMessage);

            let parsedBody;
            try {
                parsedBody = JSON.parse(snsMessage);
            } catch (e) {
                console.warn("Message is not valid JSON, skipping processing.", snsMessage);
                continue;
            }

            const { eventType, payload } = parsedBody;

            // In our Spring Boot code, the payload string might need parsing if it was sent as Map.toString()
            // Assume parsing logic here or that Spring Boot correctly serializes to JSON object.
            
            // Re-parse payload if necessary, assuming it's an object here.
            let userPayload: any;
            try {
                userPayload = typeof payload === "string" ? JSON.parse(payload) : payload;
            } catch (e) {
                userPayload = payload; // fallback
            }

            if (eventType === "UserCreatedEvent") {
                const { email, tempPassword } = userPayload;
                if (!email) throw new Error("Email no provided in UserCreatedEvent");

                const html = getWelcomeEmailTemplate(email, tempPassword);
                await sendEmail([email], "¡Bienvenido a nuestro sistema!", html);
                
            } else if (eventType === "UserPasswordResetEvent") {
                const { email, tempPassword } = userPayload;
                if (!email) throw new Error("Email no provided in UserPasswordResetEvent");

                const html = getResetPasswordTemplate(tempPassword);
                await sendEmail([email], "Restablecimiento de Contraseña", html);
                
            } else {
                console.log("Unhandled event type:", eventType);
            }
        }
    } catch (error) {
        console.error("Lambda execution error:", error);
        throw error;
    }
};
