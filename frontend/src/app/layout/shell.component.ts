import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="shell">
      <aside class="rail">
        <div class="brand">
          <span class="mark">GBV</span>
          <div>
            <strong>Case Portal</strong>
            <small>Staff only</small>
          </div>
        </div>
        <nav>
          <a routerLink="/dashboard" routerLinkActive="active">Overview</a>
          <a routerLink="/cases" routerLinkActive="active">Cases</a>
          @if (auth.isAdmin()) {
            <a routerLink="/audit" routerLinkActive="active">Audit log</a>
            <a routerLink="/users" routerLinkActive="active">Staff</a>
          }
        </nav>
        <div class="who">
          <span>{{ auth.profile()?.displayName }}</span>
          <small>{{ auth.profile()?.role === 'ADMIN' ? 'Administrator' : 'Case manager' }}</small>
          <button type="button" class="linkish" (click)="auth.logout()">Sign out</button>
        </div>
      </aside>
      <main class="canvas">
        <router-outlet />
      </main>
    </div>
  `
})
export class ShellComponent {
  constructor(readonly auth: AuthService) {}
}
