import { NgModule, Optional, SkipSelf } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpErrorInterceptor } from './interceptors/http-error.interceptor';

@NgModule({
  imports: [HttpClientModule],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true }
  ]
})
export class CoreModule {
  /** Prevent CoreModule from being imported twice */
  constructor(@Optional() @SkipSelf() parent?: CoreModule) {
    if (parent) throw new Error('CoreModule is already loaded. Import it only once in AppModule.');
  }
}
