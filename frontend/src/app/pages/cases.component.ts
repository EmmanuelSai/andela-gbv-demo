import { DatePipe } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { CaseListItem, CaseStatus, CaseType, FemicideRiskLevel, PageResponse } from '../core/models';

@Component({
  selector: 'app-cases',
  imports: [FormsModule, RouterLink, DatePipe],
  template: `
    <header class="page-head">
      <div>
        <p class="eyebrow">Intake</p>
        <h1>Cases</h1>
      </div>
    </header>

    <form class="filters" (ngSubmit)="load(0)">
      <label>
        Status
        <select name="status" [(ngModel)]="status">
          <option value="">All</option>
          @for (option of statuses; track option) {
            <option [value]="option">{{ option }}</option>
          }
        </select>
      </label>
      <label>
        Type
        <select name="type" [(ngModel)]="type">
          <option value="">All</option>
          @for (option of types; track option) {
            <option [value]="option">{{ option }}</option>
          }
        </select>
      </label>
      <label>
        Risk
        <select name="risk" [(ngModel)]="risk">
          <option value="">All</option>
          @for (option of risks; track option) {
            <option [value]="option">{{ option }}</option>
          }
        </select>
      </label>
      <label>
        Assigned to
        <input name="assignedTo" [(ngModel)]="assignedTo" placeholder="username" />
      </label>
      <button type="submit">Filter</button>
    </form>

    @if (error()) {
      <p class="error">{{ error() }}</p>
    }

    <section class="panel">
      <table>
        <thead>
          <tr>
            <th>Opened</th>
            <th>Type</th>
            <th>Status</th>
            <th>Risk</th>
            <th>Assigned</th>
          </tr>
        </thead>
        <tbody>
          @for (c of page()?.content ?? []; track c.id) {
            <tr [routerLink]="['/cases', c.id]">
              <td>{{ c.createdAt | date:'medium' }}</td>
              <td>{{ c.type }}</td>
              <td><span class="pill" [class]="c.status">{{ c.status }}</span></td>
              <td><span class="pill" [class]="c.femicideRisk">{{ c.femicideRisk }}</span></td>
              <td>{{ c.assignedTo || '—' }}</td>
            </tr>
          }
        </tbody>
      </table>
      @if (!page()?.content?.length) {
        <p class="muted">No cases match these filters.</p>
      }
      <div class="pager">
        <button type="button" [disabled]="(page()?.page ?? 0) === 0" (click)="load((page()?.page ?? 1) - 1)">Previous</button>
        <span>Page {{ (page()?.page ?? 0) + 1 }} of {{ page()?.totalPages || 1 }}</span>
        <button type="button" [disabled]="((page()?.page ?? 0) + 1) >= (page()?.totalPages || 1)" (click)="load((page()?.page ?? 0) + 1)">Next</button>
      </div>
    </section>
  `
})
export class CasesComponent implements OnInit {
  readonly statuses: CaseStatus[] = ['NEW', 'UNDER_REVIEW', 'REFERRED', 'ESCALATED', 'CLOSED'];
  readonly types: CaseType[] = ['SURVIVOR', 'WITNESS'];
  readonly risks: FemicideRiskLevel[] = ['UNKNOWN', 'LOW', 'ELEVATED', 'IMMINENT'];

  status: CaseStatus | '' = '';
  type: CaseType | '' = '';
  risk: FemicideRiskLevel | '' = '';
  assignedTo = '';
  readonly page = signal<PageResponse<CaseListItem> | null>(null);
  readonly error = signal('');

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.load(0);
  }

  load(pageIndex: number): void {
    this.error.set('');
    this.api.listCases({
      status: this.status,
      type: this.type,
      risk: this.risk,
      assignedTo: this.assignedTo.trim(),
      page: pageIndex,
      size: 20
    }).subscribe({
      next: (result) => this.page.set(result),
      error: () => this.error.set('Could not load cases.')
    });
  }
}
