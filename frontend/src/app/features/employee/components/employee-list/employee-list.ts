import { Component, OnInit, inject, signal } from '@angular/core';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { Auth } from '../../../../core/auth/services/auth';
import { Employee } from '../../../services/employee';
import { EmployeeResponse } from '../../../../shared/interfaces/employee.interfaces';

@Component({
  selector: 'app-employee-list',
  imports: [MatPaginatorModule, MatProgressSpinnerModule],
  templateUrl: './employee-list.html',
  styleUrl: './employee-list.scss',
})
export class EmployeeList implements OnInit {
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);
  private readonly employeeService = inject(Employee);

  readonly isLoggingOut = signal(false);
  readonly isInitialLoading = signal(true);
  readonly isLoadingEmployees = signal(false);
  readonly employees = signal<EmployeeResponse[]>([]);
  readonly totalElements = signal(0);
  readonly pageIndex = signal(0);
  readonly pageSize = signal(50);

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.isLoadingEmployees.set(true);
    this.employeeService.getEmployees(this.pageIndex(), this.pageSize()).pipe(
      finalize(() => {
        this.isInitialLoading.set(false);
        this.isLoadingEmployees.set(false);
      }),
    ).subscribe({
      next: (response) => {
        this.employees.set(response.content);
        this.totalElements.set(response.totalElements);
        this.pageIndex.set(response.number);
        this.pageSize.set(response.size);
      },
      error: () => {
        this.employees.set([]);
        this.totalElements.set(0);
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadEmployees();
  }

  logout(): void {
    if (this.isLoggingOut()) {
      return;
    }

    this.isLoggingOut.set(true);

    this.auth.logout().pipe(
      finalize(() => this.isLoggingOut.set(false)),
    ).subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: () => this.router.navigateByUrl('/login'),
    });
  }
}
