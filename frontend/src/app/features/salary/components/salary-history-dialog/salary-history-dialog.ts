import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs/operators';
import { Salary, SalaryResponse } from '../../services/salary';

export interface SalaryHistoryDialogData {
  currencyIcon: (currency: string) => string;
  employeeId: number;
  employeeName: string;
  onSalaryUpdated?: () => void;
}

@Component({
  selector: 'app-salary-history-dialog',
  imports: [DatePipe, DecimalPipe, MatDialogModule, MatProgressSpinnerModule],
  templateUrl: './salary-history-dialog.html',
  styleUrl: './salary-history-dialog.scss',
})
export class SalaryHistoryDialog {
  private readonly salaryService = inject(Salary);
  private readonly dialogRef = inject(MatDialogRef<SalaryHistoryDialog>);
  readonly data = inject<SalaryHistoryDialogData>(MAT_DIALOG_DATA);

  readonly isLoading = signal(true);
  readonly isUpdating = signal(false);
  readonly latestSalaryAmount = signal('');
  readonly latestSalaryCurrency = signal('');
  readonly salaries = signal<SalaryResponse[]>([]);

  constructor() {
    this.loadSalaryHistory();
  }

  close(): void {
    this.dialogRef.close();
  }

  currencyIcon(currency: string): string {
    return this.data.currencyIcon(currency);
  }

  onLatestSalaryAmountChange(amount: string): void {
    this.latestSalaryAmount.set(amount);
  }

  updateSalary(): void {
    const amount = Number(this.latestSalaryAmount());
    const currency = this.latestSalaryCurrency();

    if (!amount || !currency || this.isUpdating()) {
      return;
    }

    this.isUpdating.set(true);

    this.salaryService.addEmployeeSalary(this.data.employeeId, { amount, currency }).pipe(
      finalize(() => this.isUpdating.set(false)),
    ).subscribe({
      next: () => {
        this.loadSalaryHistory();
        this.data.onSalaryUpdated?.();
      },
    });
  }

  private loadSalaryHistory(): void {
    this.isLoading.set(true);

    this.salaryService.getEmployeeSalaryHistory(this.data.employeeId).pipe(
      finalize(() => this.isLoading.set(false)),
    ).subscribe({
      next: (response) => {
        this.salaries.set(response);
        this.setLatestSalary(response[0]);
      },
      error: () => this.salaries.set([]),
    });
  }

  private setLatestSalary(salary?: SalaryResponse): void {
    this.latestSalaryAmount.set(salary?.amount?.toString() ?? '');
    this.latestSalaryCurrency.set(salary?.currency ?? '');
  }
}
