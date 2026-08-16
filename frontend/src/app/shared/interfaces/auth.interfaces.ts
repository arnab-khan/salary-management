export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  username: string;
  authenticated: boolean;
}
