import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Planet } from '../models/planet.model';
import { PageDto } from '../models/page.model';
import { Observable } from 'rxjs';
import { PageQuery } from './people.service';

@Injectable({ providedIn: 'root' })
export class PlanetsService {
  private readonly api = inject(ApiService);

  /**
   * Fetch a paged list of planets.
   * dir: 'asc' | 'desc' ensures compatibility with ApiService.getPage.
   */
  list(query: PageQuery): Observable<PageDto<Planet>> {
    return this.api.getPage<Planet>('planets', query);
  }
}
