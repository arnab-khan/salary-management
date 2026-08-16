import { Component } from '@angular/core';
import { EmployeeList } from '../../components/employee-list/employee-list';

@Component({
  selector: 'app-employee-page',
  imports: [EmployeeList],
  templateUrl: './employee-page.html',
  styleUrl: './employee-page.scss',
})
export class EmployeePage {}
