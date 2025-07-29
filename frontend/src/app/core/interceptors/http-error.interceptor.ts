import {
  HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse
} from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  private snack = inject(MatSnackBar);

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((err: unknown) => {
        if (err instanceof HttpErrorResponse) {
          const msg = err.error?.message ?? err.statusText ?? 'Unknown error';
          this.snack.open(`Error ${err.status}: ${msg}`, 'Close', { duration: 5000 });
        }
        return throwError(() => err);
      })
    );
  }
}
