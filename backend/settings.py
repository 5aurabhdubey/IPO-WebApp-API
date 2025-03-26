import os
import environ
from dotenv import load_dotenv
from pathlib import Path

DEFAULT_AUTO_FIELD = 'django.db.models.BigAutoField'
# Load environment variables from .env file
load_dotenv()

# Initialize django-environ
env = environ.Env()
environ.Env.read_env()

# Set DJANGO_SETTINGS_MODULE dynamically
DJANGO_SETTINGS_MODULE = env("DJANGO_SETTINGS_MODULE", default="backend.settings")

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = env.bool("DEBUG", default=True)

# Define allowed hosts (modify if deploying)
ALLOWED_HOSTS = env.list("ALLOWED_HOSTS", default=["127.0.0.1", "localhost"])

SECRET_KEY = env("SECRET_KEY", default="your-default-secret-key")

# API Keys (Securely loaded from .env)
API_KEYS = {
    "TWELVE_DATA": env("TWELVE_DATA_API_KEY", default=""),
    "FMP": env("FMP_API_KEY", default=""),
    "PLG": env("PLG_API_KEY", default=""),
    "BRANDFETCH": env("BRANDFETCH_API_KEY", default=""),
}

# Required Django settings
INSTALLED_APPS = [
    "django.contrib.admin",
    "django.contrib.auth",
    "django.contrib.contenttypes",
    "django.contrib.sessions",
    "django.contrib.messages",
    "django.contrib.staticfiles",
    "rest_framework",  # Ensure Django REST Framework is installed
    "backend",
    'corsheaders',#To access api in different port
]

MIDDLEWARE = [
    "django.middleware.security.SecurityMiddleware",
    "django.contrib.sessions.middleware.SessionMiddleware",
    "django.middleware.common.CommonMiddleware",
    "django.middleware.csrf.CsrfViewMiddleware",
    "django.contrib.auth.middleware.AuthenticationMiddleware",
    "django.contrib.messages.middleware.MessageMiddleware",
    "django.middleware.clickjacking.XFrameOptionsMiddleware",
    'corsheaders.middleware.CorsMiddleware',#cors middleware
    'django.middleware.common.CommonMiddleware',
]

CORS_ALLOWED_ORIGINS = [
    'http://localhost:3000',#Specify origins to access api data
    'http://127.0.0.1:3000',
    'http://127.0.0.1:3001',
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
BASE_DIR = Path(__file__).resolve().parent.parent
STATIC_URL = "/static/"
STATICFILES_DIRS = [BASE_DIR / "frontend"]
