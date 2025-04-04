import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { TokenService } from '../services/token/token.service';
import { Router } from '@angular/router';
export const authGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);
  if (tokenService.isTokenNonValid()) {
    router.navigate(['login']);
    return false;
  }
  return true;
};
