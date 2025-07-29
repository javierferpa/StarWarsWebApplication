import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Environment } from '../../../environments/environment.interface';
import { environment } from '../../../environments/environment';
import { PageDto } from '../models/page.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = (environment as Environment).apiBaseUrl;

  /** GET /api/{resource} con parámetros de paginación/búsqueda */
  getPage<T>(
    resource: 'people' | 'planets',
    params: { page: number; size: number; sort: string; dir: 'asc' | 'desc'; search?: string }
  ): Observable<PageDto<T>> {
    const httpParams = new HttpParams({ fromObject: { ...params } });
    return this.http.get<PageDto<T>>(`${this.base}/${resource}`, { params: httpParams });
  }
}
