import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
@Injectable({
  providedIn: 'root',
})
export class TokenService {
  constructor() {}
  get token() {
    return localStorage.getItem('token') as string;
  }
  set token(token: string) {
    localStorage.setItem('token', token);
  }
  isTokenNonValid() {
    return !this.isTokenValid();
  }
  isTokenValid() {
    const token = this.token;
    if (!token) {
      return false;
    }
    const jwtHelper = new JwtHelperService();
    const isexpired = jwtHelper.isTokenExpired(token);
    if (isexpired) {
      localStorage.clear();
      return false;
    }
    return true;
  }
}
