import {ApplicationConfig} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideAnimations} from '@angular/platform-browser/animations';
import {
  provideHttpClient,
  withInterceptorsFromDi
} from '@angular/common/http';

import {APP_ROUTES} from './app.routes';
import {HttpErrorInterceptor} from './core/interceptors/http-error.interceptor';
import {HTTP_INTERCEPTORS} from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(APP_ROUTES),

    provideHttpClient(withInterceptorsFromDi()),

    {provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true},

    provideAnimations()
  ]
};
