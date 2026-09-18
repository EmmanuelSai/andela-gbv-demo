import { Routes } from '@angular/router';
import { adminGuard, authGuard } from './core/auth.guard';
import { ShellComponent } from './layout/shell.component';
import { AuditComponent } from './pages/audit.component';
import { CaseDetailComponent } from './pages/case-detail.component';
import { CasesComponent } from './pages/cases.component';
import { DashboardComponent } from './pages/dashboard.component';
import { LoginComponent } from './pages/login.component';
import { UsersComponent } from './pages/users.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'cases', component: CasesComponent },
      { path: 'cases/:id', component: CaseDetailComponent },
      { path: 'audit', component: AuditComponent, canActivate: [adminGuard] },
      { path: 'users', component: UsersComponent, canActivate: [adminGuard] }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
