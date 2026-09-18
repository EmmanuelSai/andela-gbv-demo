import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  template: `
    <section class="login-wrap">
      <div class="login-card">
        <p class="eyebrow">Protected workspace</p>
        <h1>GBV case portal</h1>
        <p class="lede">
          Sign in to review reports, assign follow-up, and keep a confidential audit trail.
          This space is for authorised case staff only.
        </p>
        <form (ngSubmit)="submit()">
          <label>
            Username
            <input name="username" [(ngModel)]="username" autocomplete="username" required />
          </label>
          <label>
            Password
            <input name="password" type="password" [(ngModel)]="password" autocomplete="current-password" required />
          </label>
          @if (error()) {
            <p class="error">{{ error() }}</p>
          }
          <button type="submit" [disabled]="busy()">{{ busy() ? 'Signing in…' : 'Sign in' }}</button>
        </form>
        <p class="hint">Use the staff username and password from your server environment.</p>
      </div>
    </section>
  `
})
export class LoginComponent {
  username = '';
  password = '';
  readonly busy = signal(false);
  readonly error = signal('');

  constructor(
    private readonly api: ApiService,
    private readonly auth: AuthService,
    private readonly router: Router
  ) {
    if (this.auth.isLoggedIn()) {
      void this.router.navigate(['/dashboard']);
    }
  }

  submit(): void {
    this.error.set('');
    this.busy.set(true);
    this.api.login(this.username.trim(), this.password).subscribe({
      next: (res) => {
        this.auth.setSession(res);
        void this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.busy.set(false);
        this.error.set('Those credentials were not accepted.');
      }
    });
  }
}
