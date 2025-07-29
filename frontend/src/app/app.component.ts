import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterOutlet }    from '@angular/router';

@Component({
  selector: 'sw-root',
  standalone: true,
  imports: [
    MatToolbarModule,
    RouterOutlet
  ],
  template: `
    <mat-toolbar color="primary">StarWarsDB</mat-toolbar>
    <router-outlet></router-outlet>
  `
})
export class AppComponent {}
