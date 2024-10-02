import {inject} from '@angular/core';
import {CanActivateFn} from '@angular/router';
import {Router} from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
    const router = inject(Router);
    const token = localStorage.getItem('user_token');

    if (token) {
        return true;
    } else {
        return router.navigate(['/authentication']);
    }
    return true;
};
