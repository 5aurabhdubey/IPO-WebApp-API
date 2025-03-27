from rest_framework import generics, status
from rest_framework.response import Response
from rest_framework_simplejwt.views import TokenObtainPairView
from rest_framework.permissions import AllowAny
from .serializers import UserSignupSerializer, CustomTokenObtainPairSerializer
from .permissions import IsAdminUser, IsUserOrAdmin
from django.contrib.auth import get_user_model

User = get_user_model()

class SignupView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = UserSignupSerializer
    permission_classes = [AllowAny]  # Public access

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        return Response({
            'user': {'username': user.username, 'email': user.email, 'role': user.role},
            'message': 'User created successfully'
        }, status=status.HTTP_201_CREATED)

class LoginView(TokenObtainPairView):
    serializer_class = CustomTokenObtainPairSerializer
    permission_classes = [AllowAny]  # Public access

# Example protected view (e.g., for admin-only access)
class AdminOnlyView(generics.ListAPIView):
    queryset = User.objects.all()
    serializer_class = UserSignupSerializer
    permission_classes = [IsAdminUser]  # Only admins can access

# Example user-or-admin view
class UserProfileView(generics.RetrieveAPIView):
    serializer_class = UserSignupSerializer
    permission_classes = [IsUserOrAdmin]  # Users or admins can access

    def get_object(self):
        return self.request.user
