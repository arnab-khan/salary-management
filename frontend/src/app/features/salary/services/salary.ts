import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface SalaryResponse {
  id: number;
  amount: number;
  currency: string;
  createdAt: string;
}

export interface SalaryRequest {
  amount: number;
  currency: string;
}

@Injectable({
  providedIn: 'root',
})
export class Salary {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/salaries`;

  getEmployeeSalaryHistory(employeeId: number): Observable<SalaryResponse[]> {
    return this.http.get<SalaryResponse[]>(`${this.apiUrl}/employee/${employeeId}`);
  }

  addEmployeeSalary(employeeId: number, request: SalaryRequest): Observable<SalaryResponse> {
    return this.http.post<SalaryResponse>(`${this.apiUrl}/employee/${employeeId}`, request);
  }
}
