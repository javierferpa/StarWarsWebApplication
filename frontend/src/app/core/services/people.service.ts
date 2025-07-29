import { inject, Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Person } from '../models/people.model';
import { PageDto } from '../models/page.model';
import { Observable } from 'rxjs';

export interface PageQuery {
  page:   number;
  size:   number;
  sort:   string;
  dir:    'asc' | 'desc';
  search: string;
}

@Injectable({ providedIn: 'root' })
export class PeopleService {
  private readonly api = inject(ApiService);

  /**
   * Fetch a paged list of people.
   * 'dir' is strictly 'asc' or 'desc' so TS knows it's valid.
   */
  list(query: PageQuery): Observable<PageDto<Person>> {
    return this.api.getPage<Person>('people', query);
  }
}
