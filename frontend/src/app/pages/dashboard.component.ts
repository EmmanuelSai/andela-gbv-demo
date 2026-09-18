import { DatePipe } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ApiService } from '../core/api.service';
import { DashboardSummary } from '../core/models';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, DatePipe],
  template: `
    <header class="page-head">
      <div>
        <p class="eyebrow">Today</p>
        <h1>Case overview</h1>
      </div>
    </header>

    @if (error()) {
      <p class="error">{{ error() }}</p>
    }

    @if (summary(); as s) {
      <section class="kpis">
        <article>
          <small>Open caseload</small>
          <strong>{{ s.totalCases }}</strong>
        </article>
        <article class="warn">
          <small>Imminent risk</small>
          <strong>{{ s.imminentCount }}</strong>
        </article>
        <article>
          <small>Escalated</small>
          <strong>{{ s.escalatedCount }}</strong>
        </article>
        <article>
          <small>New</small>
          <strong>{{ s.byStatus['NEW'] || 0 }}</strong>
        </article>
      </section>

      <section class="split">
        <article class="panel">
          <h2>Priority queue</h2>
          @if (!s.priorityQueue.length) {
            <p class="muted">No imminent or escalated cases right now.</p>
          } @else {
            <table>
              <thead>
                <tr>
                  <th>Opened</th>
                  <th>Type</th>
                  <th>Status</th>
                  <th>Risk</th>
                </tr>
              </thead>
              <tbody>
                @for (c of s.priorityQueue; track c.id) {
                  <tr [routerLink]="['/cases', c.id]">
                    <td>{{ c.createdAt | date:'medium' }}</td>
                    <td>{{ c.type }}</td>
                    <td><span class="pill" [class]="c.status">{{ c.status }}</span></td>
                    <td><span class="pill" [class]="c.femicideRisk">{{ c.femicideRisk }}</span></td>
                  </tr>
                }
              </tbody>
            </table>
          }
        </article>
        <article class="panel">
          <h2>By status</h2>
          <ul class="stat-list">
            @for (entry of statusEntries(s); track entry[0]) {
              <li><span>{{ entry[0] }}</span><strong>{{ entry[1] }}</strong></li>
            }
          </ul>
          <h2>By type</h2>
          <ul class="stat-list">
            @for (entry of typeEntries(s); track entry[0]) {
              <li><span>{{ entry[0] }}</span><strong>{{ entry[1] }}</strong></li>
            }
          </ul>
        </article>
      </section>
    }
  `
})
export class DashboardComponent implements OnInit {
  readonly summary = signal<DashboardSummary | null>(null);
  readonly error = signal('');

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.api.dashboard().subscribe({
      next: (s) => this.summary.set(s),
      error: () => this.error.set('Could not load the dashboard.')
    });
  }

  statusEntries(s: DashboardSummary): [string, number][] {
    return Object.entries(s.byStatus);
  }

  typeEntries(s: DashboardSummary): [string, number][] {
    return Object.entries(s.byType);
  }
}
