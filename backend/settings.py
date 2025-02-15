import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = os.getenv("DEBUG", "True") == "True"  # Reads from .env, defaults to True

# Define allowed hosts (modify if deploying)
ALLOWED_HOSTS = os.getenv("ALLOWED_HOSTS", "127.0.0.1,localhost").split(",")

SECRET_KEY = os.getenv("SECRET_KEY", "your-default-secret-key")


# API Keys (Securely loaded from .env)
API_KEYS = {
    "TWELVE_DATA": os.getenv("TWELVE_DATA_API_KEY"),
    "FMP": os.getenv("FMP_API_KEY"),
    "BRANDFETCH": os.getenv("BRANDFETCH_API_KEY"),
}

# Required Django settings
INSTALLED_APPS = [
    "django.contrib.admin",
    "django.contrib.auth",
    "django.contrib.contenttypes",
    "django.contrib.sessions",
    "django.contrib.messages",
    "django.contrib.staticfiles",
    "rest_framework", 
    "backend",           # Ensure Django REST Framework is installed
]

MIDDLEWARE = [
    "django.middleware.security.SecurityMiddleware",
    "django.contrib.sessions.middleware.SessionMiddleware",
    "django.middleware.common.CommonMiddleware",
    "django.middleware.csrf.CsrfViewMiddleware",
    "django.contrib.auth.middleware.AuthenticationMiddleware",
    "django.contrib.messages.middleware.MessageMiddleware",
    "django.middleware.clickjacking.XFrameOptionsMiddleware",
]

ROOT_URLCONF = "backend.urls"

TEMPLATES = [
    {
        "BACKEND": "django.template.backends.django.DjangoTemplates",
        "DIRS": [],
        "APP_DIRS": True,
        "OPTIONS": {
            "context_processors": [
                "django.template.context_processors.debug",
                "django.template.context_processors.request",
                "django.contrib.auth.context_processors.auth",
                "django.contrib.messages.context_processors.messages",
            ],
        },
    },
]

WSGI_APPLICATION = "backend.wsgi.application"

# Database (If needed)
DATABASES = {
    "default": {
        "ENGINE": "django.db.backends.sqlite3",  # Change if using PostgreSQL, MySQL, etc.
        "NAME": os.path.join(os.path.dirname(__file__), "db.sqlite3"),
    }
}

# Static files (CSS, JavaScript, Images)
STATIC_URL = "/static/"
