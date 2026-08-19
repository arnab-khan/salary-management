import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SalaryHistoryDialog } from './salary-history-dialog';

describe('SalaryHistoryDialog', () => {
  let component: SalaryHistoryDialog;
  let fixture: ComponentFixture<SalaryHistoryDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SalaryHistoryDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(SalaryHistoryDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
