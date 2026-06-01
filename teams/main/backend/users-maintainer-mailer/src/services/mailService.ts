import { SESClient, SendEmailCommand } from "@aws-sdk/client-ses";

const ses = new SESClient({ region: process.env.AWS_REGION || "us-east-1" });
const SOURCE_EMAIL = process.env.SOURCE_EMAIL || "no-reply@tudominio.com";

export const sendEmail = async (toAddresses: string[], subject: string, htmlBody: string) => {
    const command = new SendEmailCommand({
        Destination: {
            ToAddresses: toAddresses,
        },
        Message: {
            Body: {
                Html: {
                    Charset: "UTF-8",
                    Data: htmlBody,
                },
            },
            Subject: {
                Charset: "UTF-8",
                Data: subject,
            },
        },
        Source: SOURCE_EMAIL,
    });

    try {
        await ses.send(command);
        console.log(`Email sent successfully to ${toAddresses.join(', ')}`);
    } catch (error) {
        console.error("Error sending email:", error);
        throw error;
    }
};
