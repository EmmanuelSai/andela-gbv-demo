import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { StaffRole, StaffUser } from '../core/models';

@Component({
  selector: 'app-users',
  imports: [FormsModule],
  template: `
    <header class="page-head">
      <div>
        <p class="eyebrow">Administrator</p>
        <h1>Staff accounts</h1>
      </div>
    </header>

    <section class="split">
      <article class="panel">
        <h2>People</h2>
        @if (error()) {
          <p class="error">{{ error() }}</p>
        }
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Username</th>
              <th>Role</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            @for (user of users(); track user.id) {
              <tr>
                <td>{{ user.displayName }}</td>
                <td>{{ user.username }}</td>
                <td>{{ user.role }}</td>
                <td>{{ user.enabled ? 'Active' : 'Disabled' }}</td>
                <td>
                  <button type="button" class="linkish" (click)="toggle(user)">
                    {{ user.enabled ? 'Disable' : 'Enable' }}
                  </button>
                </td>
              </tr>
            }
          </tbody>
        </table>
      </article>

      <article class="panel">
        <h2>Add staff</h2>
        <form (ngSubmit)="create()">
          <label>Display name <input name="displayName" [(ngModel)]="displayName" required /></label>
          <label>Username <input name="username" [(ngModel)]="username" required /></label>
          <label>Password <input name="password" type="password" [(ngModel)]="password" required minlength="8" /></label>
          <label>
            Role
            <select name="role" [(ngModel)]="role">
              <option value="CASE_MANAGER">CASE_MANAGER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </label>
          <button type="submit">Create account</button>
        </form>
      </article>
    </section>
  `
})
export class UsersComponent implements OnInit {
  readonly users = signal<StaffUser[]>([]);
  readonly error = signal('');
  displayName = '';
  username = '';
  password = '';
  role: StaffRole = 'CASE_MANAGER';

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.api.users().subscribe({
      next: (people) => this.users.set(people),
      error: () => this.error.set('Could not load staff users.')
    });
  }

  create(): void {
    this.api.createUser({
      username: this.username.trim(),
      password: this.password,
      displayName: this.displayName.trim(),
      role: this.role
    }).subscribe({
      next: () => {
        this.displayName = '';
        this.username = '';
        this.password = '';
        this.role = 'CASE_MANAGER';
        this.refresh();
      },
      error: (err) => this.error.set(err.error?.error || 'Could not create user.')
    });
  }

  toggle(user: StaffUser): void {
    this.api.setUserEnabled(user.id, !user.enabled).subscribe({
      next: () => this.refresh(),
      error: () => this.error.set('Could not update user.')
    });
  }
}
