/* src/app/features/planets/planet-list.component.ts */
import { Component, OnInit } from '@angular/core';
import { PlanetsService }     from '../../core/services/planets.service';
import { Planet }             from '../../core/models/planet.model';
import { PageDto }            from '../../core/models/page.model';
import {MatCard} from '@angular/material/card';
import {MatToolbar} from '@angular/material/toolbar';
import {SearchBarComponent} from '../../shared/search-bar/search-bar.component';
import {DataTableComponent} from '../../shared/data-table/data-table.component';
import {MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef} from '@angular/material/table';
import {DatePipe} from '@angular/common';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'sw-planet-list',
  templateUrl: './planet-list.component.html',
  imports: [
    MatCard,
    MatToolbar,
    SearchBarComponent,
    DataTableComponent,
    MatColumnDef,
    MatCell,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCellDef,
    DatePipe,
    MatProgressSpinner,
    DecimalPipe
  ],
  styleUrls: ['./planet-list.component.scss']
})
export class PlanetListComponent implements OnInit {
  displayed = ['name','population','created'];
  sortable = ['name','population', 'created'];
  data: Planet[] = [];
  total = 0;

  pageSize = 15;

  query = {
    page:   0,
    size:   this.pageSize,
    sort:   'name',
    dir:    'asc' as 'asc'|'desc',
    search: ''
  };

  loading = false;

  constructor(private planetsSvc: PlanetsService){}

  ngOnInit() { this.load(); }

  onSearch(txt: string) {
    this.query.page = 0;
    this.query.search = txt;
    this.load();
  }

  onSort(e: { active:string; direction:string }) {
    if (!e.direction) return;
    this.query.sort = e.active;
    this.query.dir  = e.direction as 'asc'|'desc';
    this.load();
  }

  onPage(e: { pageIndex:number; pageSize:number }) {
    this.query.page = e.pageIndex;
    this.query.size = e.pageSize;
    this.pageSize   = e.pageSize;
    this.load();
  }

  private load() {
    this.loading = true;
    this.planetsSvc.list(this.query).subscribe({
      next: (page: PageDto<Planet>) => {
        this.data    = page.items;
        this.total   = page.total;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
