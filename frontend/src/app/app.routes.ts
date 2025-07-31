import { Routes } from '@angular/router';

export const APP_ROUTES: Routes = [
  { path: '',        redirectTo: 'people', pathMatch: 'full' },
  {
    path: 'people',
    loadComponent: () =>
      import('./features/people/people-list.component')
        .then(m => m.PeopleListComponent)
  },
  {
    path: 'planets',
    loadComponent: () =>
      import('./features/planets/planet-list.component')
        .then(m => m.PlanetListComponent)
  },
  { path: '**', redirectTo: 'people' }
];
