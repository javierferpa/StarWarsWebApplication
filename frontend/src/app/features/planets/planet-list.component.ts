import { Component, OnInit }        from '@angular/core';
import { CommonModule }             from '@angular/common';
import { MatTableModule }           from '@angular/material/table';
import { MatPaginatorModule }       from '@angular/material/paginator';
import { MatSortModule }            from '@angular/material/sort';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SearchBarComponent }       from '../../shared/search-bar/search-bar.component';
import { PlanetsService }           from '../../core/services/planets.service';
import { Planet }                   from '../../core/models/planet.model';
import { PageDto }                  from '../../core/models/page.model';
import {DataTableComponent} from '../../shared/data-table/data-table.component';
import {MatToolbar} from '@angular/material/toolbar';
import {MatCard} from '@angular/material/card';

@Component({
  selector:    'sw-planet-list',
  standalone:  true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    SearchBarComponent,
    DataTableComponent,
    MatToolbar,
    MatCard
  ],
  templateUrl: './planet-list.component.html',
  styleUrls:   ['./planet-list.component.scss']
})
export class PlanetListComponent implements OnInit {
  displayed = ['name','population','created'];
  data:      Planet[] = [];
  total    = 0;
  loading  = false;

  query = {
    page:   0,
    size:   15,
    sort:   'name',
    dir:    'asc' as 'asc' | 'desc',
    search: ''
  };

  constructor(private planetsSvc: PlanetsService) {}

  ngOnInit() {
    this.load();
  }

  onSearch(txt: string) {
    this.query.page   = 0;
    this.query.search = txt;
    this.load();
  }

  onSort(e: { active:string; direction:string }) {
    if (!e.direction) return;
    this.query.sort = e.active;
    this.query.dir  = e.direction as 'asc'|'desc';
    this.load();
  }

  onPage(e: { pageIndex:number }) {
    this.query.page = e.pageIndex;
    this.load();
  }

  private load() {
    this.loading = true;
    this.planetsSvc.list(this.query).subscribe({
      next:   (page:PageDto<Planet>) => {
        this.data    = page.items;
        this.total   = page.total;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
