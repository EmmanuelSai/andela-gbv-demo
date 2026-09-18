import { Injectable, computed, signal } from '@angular/core';
import { Router } from '@angular/router';
import { LoginResponse, StaffRole } from './models';

const TOKEN_KEY = 'gbv.portal.token';
const PROFILE_KEY = 'gbv.portal.profile';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  private readonly profileSignal = signal<Omit<LoginResponse, 'token'> | null>(readProfile());

  readonly token = this.tokenSignal.asReadonly();
  readonly profile = this.profileSignal.asReadonly();
  readonly isLoggedIn = computed(() => !!this.tokenSignal());
  readonly isAdmin = computed(() => this.profileSignal()?.role === 'ADMIN');

  constructor(private readonly router: Router) {}

  setSession(response: LoginResponse): void {
    if (response.token) {
      localStorage.setItem(TOKEN_KEY, response.token);
      this.tokenSignal.set(response.token);
    }
    const profile = {
      role: response.role,
      displayName: response.displayName,
      username: response.username
    };
    localStorage.setItem(PROFILE_KEY, JSON.stringify(profile));
    this.profileSignal.set(profile);
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(PROFILE_KEY);
    this.tokenSignal.set(null);
    this.profileSignal.set(null);
    void this.router.navigate(['/login']);
  }

  role(): StaffRole | null {
    return this.profileSignal()?.role ?? null;
  }
}

function readProfile(): Omit<LoginResponse, 'token'> | null {
  const raw = localStorage.getItem(PROFILE_KEY);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw) as Omit<LoginResponse, 'token'>;
  } catch {
    return null;
  }
}
