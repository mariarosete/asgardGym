# 📱 Asgard Gym – App móvil para gimnasios

![Banner Asgard](https://github.com/mariarosete/asgardGym/blob/main/bannerAsgard.png?raw=true)

**Asgard Gym** es una aplicación Android desarrollada para mejorar la experiencia de gestión en centros deportivos. Permite a los usuarios consultar y reservar actividades fácilmente, mientras el personal puede gestionar usuarios y actividades de forma eficiente.

---

## 🛠 Tecnologías utilizadas

Kotlin · Android Studio · SQLite · Material Design · GitHub

---

## 🚀 Funcionalidades destacadas

### 👤 Para usuarios del gimnasio:

- **Inicio de sesión rápido** mediante DNI y correo electrónico.
- **Agenda semanal visual** con navegación por semanas, colores por tipo de actividad y filtros por nombre, monitor o tipo.
- **Reserva de actividades en tiempo real** con validaciones automáticas.
- **Consulta y cancelación de reservas activas**, con actualización automática de plazas.
- **Perfil personal editable**, incluyendo estadísticas (número de reservas y última fecha).

### 🧑‍💼 Para personal del gimnasio (panel administración):

- Gestión de usuarios: alta, edición y baja.
- Administración de actividades: creación, edición, eliminación y asignación de plazas.
- Registro histórico de reservas mediante triggers automáticos.

---

## 💻 Cómo ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/mariarosete/asgardGym.git
```

### 2. Abrir en Android Studio

1. Abre Android Studio y selecciona **File > Open**.
2. Navega hasta la carpeta del proyecto y ábrela.
3. Espera a que Gradle sincronice automáticamente.

### 3. Ejecutar la aplicación

- Conecta un dispositivo Android o inicia un emulador.
- Haz clic en ▶️ (Run).
- La app se instalará y ejecutará automáticamente.

📌 **Nota**: Si no aparecen datos precargados, incrementa la constante `DATABASE_VERSION` en la clase `DatabaseHelper` para forzar la reinicialización de la base de datos.

---
### 📦 Instalación directa (APK)

También puedes instalar la aplicación directamente desde el archivo `.apk` incluido en el repositorio, sin necesidad de compilarla.

📁 El archivo se encuentra en la carpeta:  
```
APK/asgard_gym.apk
```

Solo necesitas transferirlo a tu dispositivo Android y ejecutarlo para instalar la app (puede requerir activar "orígenes desconocidos").

---

## 🔐 Inicio de sesión de prueba

Puedes acceder con los siguientes usuarios precargados:

| DNI         | Correo electrónico         |
|-------------|----------------------------|
| 12345678A   | admin@asgardgym.com        |
| 12345678B   | maria@email.com            |

> No se requiere contraseña.

---

## 📱 Capturas de pantalla

### 🏋️ Inicio y navegación  
![Inicio](https://github.com/mariarosete/asgardGym/blob/main/screenshots/01_inicio.png?raw=true)  
![Menú](https://github.com/mariarosete/asgardGym/blob/main/screenshots/02_menu.png?raw=true)

### 📅 Agenda de actividades  
![Agenda](https://github.com/mariarosete/asgardGym/blob/main/screenshots/03_agenda.png?raw=true)

### 📋 Detalles y reservas  
![Detalle](https://github.com/mariarosete/asgardGym/blob/main/screenshots/04_detalle.png?raw=true)  
![Reserva](https://github.com/mariarosete/asgardGym/blob/main/screenshots/05_reserva.png?raw=true)

### 📚 Mis reservas  
![Reservas](https://github.com/mariarosete/asgardGym/blob/main/screenshots/06_misreservas.png?raw=true)

### 👤 Perfil de usuario  
![Perfil](https://github.com/mariarosete/asgardGym/blob/main/screenshots/07_perfil.png?raw=true)

---

## 🌐 Próximas mejoras

- Sincronización de datos mediante API REST.
- Panel de administración en la nube.
- Análisis de hábitos saludables y planes personalizados.

---

## 📩 Contacto

📧 marlarosete89@gmail.com  
🔗 [LinkedIn](https://linkedin.com/in/mariarosetesuarez)  
💻 [GitHub](https://github.com/mariarosete)
