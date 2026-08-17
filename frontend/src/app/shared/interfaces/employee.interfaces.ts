export interface EmployeeResponse {
  id: number;
  name: string;
  email: string;
  role: string;
  experience: number;
  joiningDate: string;
  country: string;
  currentSalaryAmount: number | null;
  currency: string | null;
  salaryHistoryCount: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
