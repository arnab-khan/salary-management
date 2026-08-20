import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';

import { SalaryHistoryDialog } from './salary-history-dialog';
import { Salary } from '../../services/salary';

describe('SalaryHistoryDialog', () => {
  let component: SalaryHistoryDialog;
  let fixture: ComponentFixture<SalaryHistoryDialog>;
  let salaryService: Pick<Salary, 'getEmployeeSalaryHistory' | 'addEmployeeSalary'>;

  beforeEach(async () => {
    salaryService = {
      addEmployeeSalary: vi.fn(),
      getEmployeeSalaryHistory: vi.fn().mockReturnValue(of([])),
    };

    await TestBed.configureTestingModule({
      imports: [SalaryHistoryDialog],
      providers: [
        { provide: Salary, useValue: salaryService },
        { provide: MatDialogRef, useValue: { close: vi.fn() } },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            currencyIcon: (currency: string) => currency,
            employeeId: 1,
            employeeName: 'Employee 1',
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SalaryHistoryDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
