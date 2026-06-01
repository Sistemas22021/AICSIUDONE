export const getWelcomeEmailTemplate = (email: string, tempPassword: string) => `
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f4f4f5; padding: 20px; }
        .container { background-color: #ffffff; padding: 30px; border-radius: 8px; max-width: 600px; margin: 0 auto; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .header { font-size: 24px; font-weight: bold; color: #18181b; margin-bottom: 20px; }
        .content { font-size: 16px; color: #3f3f46; line-height: 1.5; }
        .highlight { background-color: #fef08a; padding: 5px; font-weight: bold; border-radius: 4px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">¡Bienvenido al sistema!</div>
        <div class="content">
            <p>Hola,</p>
            <p>Tu cuenta ha sido creada exitosamente. Tu usuario es <strong>${email}</strong>.</p>
            <p>Para ingresar por primera vez, utiliza la siguiente contraseña temporal:</p>
            <p class="highlight">${tempPassword}</p>
            <p>El sistema te pedirá cambiarla en tu primer inicio de sesión.</p>
        </div>
    </div>
</body>
</html>
`;
