# Asgard Gym - App móvil para gimnasios

![Banner Asgard](https://github.com/mariarosete/asgardGym/blob/main/bannerAsgard.png?raw=true)

Aplicación móvil Android desarrollada en Kotlin para la gestión de centros deportivos.

Incluye:
- Sistema de reservas
- Gestión de usuarios y horarios
- Persistencia de datos con SQLite
- Diseño basado en Material Design

---

## 🎥 Demo en vídeo

👉 https://mariarosete.vercel.app/assets/AsgardGym-DimlY--h.mp4

---

## 🛠 Tecnologías utilizadas

![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)  
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)  
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)  
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)

---

## 💻 Cómo ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/mariarosete/asgardGym.git
```

### 2. Abrir en Android Studio

1. Abre Android Studio  
2. Selecciona **File > Open**  
3. Navega hasta la carpeta del proyecto  
4. Espera a que Gradle sincronice automáticamente  

### 3. Ejecutar la aplicación

- Conecta un dispositivo Android o inicia un emulador  
- Pulsa ▶️ **Run**  
- La app se instalará y ejecutará automáticamente  

📌 **Nota:**  
Si no aparecen datos precargados, incrementa la constante `DATABASE_VERSION` en la clase `DatabaseHelper`.

---

## 📦 Instalación directa (APK)

📥 **[Descargar APK](https://github.com/mariarosete/asgardGym/raw/main/appAsgardGym/APK/app-debug.apk)**  

- Ruta: `appAsgardGym/APK/app-debug.apk`  
- Puede ser necesario activar la opción **"Instalar apps de orígenes desconocidos"** en el dispositivo  

---

## Funcionalidades destacadas

### 👤 Usuario

- Reservas en tiempo real  
- Agenda semanal  
- Gestión de perfil  

### 🧑‍💼 Administración

- CRUD de usuarios  
- Gestión de actividades  
- Histórico de reservas  

---

## 🔐 Inicio de sesión de prueba

| DNI         | Email                |
|-------------|---------------------|
| 12345678A   | admin@asgardgym.com |
| 12345678B   | maria@email.com     |

---
> No se requiere contraseña.

---

## 📸 Capturas de pantalla

| Iniciar sesión | Inicio y navegación |
|-------------------|------------------------|
| ![Login](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Login.png?raw=true) | ![Inicio](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Pantalla_Principal.png?raw=true) |

| Agenda de actividades | Filtrado de actividades |
|--------------------------|----------------------------|
| ![Agenda](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Agenda.png?raw=true) | ![Filtro](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Filtro.png?raw=true) |
| ![Actividades](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Agenda_Actividades.png?raw=true) | |

| Detalles y reservas |  Mis reservas |
|------------------------|-----------------|
| ![Detalles](https://github.com/mariarosete/asgardGym/blob/main/screenshots/DEtalles.png?raw=true) | ![Reservas](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Reservas.png?raw=true) |

|  Perfil de usuario |  Ayuda |
|----------------------|---------|
| ![Usuario](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Usuario.png?raw=true) | ![Ayuda](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Ayuda.png?raw=true) |
| ![Detalles usuario](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Detalles_Usuario.png?raw=true) | |

|  Panel de administración |  Histórico |
|----------------------------|--------------|
| ![Admin](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Panel_Admin.png?raw=true) | ![Histórico](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Historico.png?raw=true) |
| ![Crud](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Crud_Actividades.png?raw=true) | ![Actividad](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Actividad.png?raw=true)
| ![Actividad](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Actividad.png?raw=true) | ![Crear usuario](https://github.com/mariarosete/asgardGym/blob/main/screenshots/Crear_Usuario.png?raw=true)

---

##  Próximas mejoras

- Sincronización de datos mediante API REST.
- Panel de administración en la nube.
- Análisis de hábitos saludables y planes personalizados.

---

## 📩 Contacto

<p align="center">
  <a href="mailto:marlarosete89@gmail.com">
    <img src="https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white" />
  </a>
  <a href="https://linkedin.com/in/mariarosetesuarez">
    <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" />
  </a>
  <a href="https://github.com/mariarosete">
    <img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" />
  </a>
   <a href="https://mariarosete.vercel.app/">
    <img src="https://img.shields.io/badge/Portfolio-000000?style=for-the-badge&logo=vercel&logoColor=white"></a>
</p>
