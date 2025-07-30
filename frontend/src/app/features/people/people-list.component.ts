/* src/app/features/people/people-list.component.ts */
import { Component, OnInit } from '@angular/core';
import { PeopleService }      from '../../core/services/people.service';
import { Person }            from '../../core/models/people.model';
import { PageDto }           from '../../core/models/page.model';
import {MatCard} from '@angular/material/card';
import {MatToolbar} from '@angular/material/toolbar';
import {SearchBarComponent} from '../../shared/search-bar/search-bar.component';
import {DataTableComponent} from '../../shared/data-table/data-table.component';
import {MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef} from '@angular/material/table';
import {DatePipe} from '@angular/common';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

@Component({
  selector: 'sw-people-list',
  templateUrl: './people-list.component.html',
  imports: [
    MatCard,
    MatToolbar,
    SearchBarComponent,
    DataTableComponent,
    MatProgressSpinner,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderCellDef,
    MatCellDef,
    DatePipe,
  ],
  styleUrls: ['./people-list.component.scss']
})
export class PeopleListComponent implements OnInit {

  displayed = ['name','height','mass','gender','created'];
  data: Person[] = [];
  total = 0;

  // ** exponemos pageSize **
  pageSize = 15;

  // query interna
  query = {
    page:   0,
    size:   this.pageSize,
    sort:   'name',
    dir:    'asc' as 'asc'|'desc',
    search: ''
  };

  loading = false;

  constructor(private peopleSvc: PeopleService) {}

  ngOnInit() { this.load(); }

  onSearch(txt: string) {
    this.query.page = 0;
    this.query.search = txt;
    this.load();
  }

  onSort(e: { active:string; direction:string }) {
    if (!e.direction) return;
    this.query.sort = e.active;
    this.query.dir = e.direction as 'asc'|'desc';
    this.load();
  }

  // ** manejamos pageIndex y pageSize del PageEvent **
  onPage(e: { pageIndex: number; pageSize: number }) {
    this.query.page = e.pageIndex;
    this.query.size = e.pageSize;
    this.pageSize   = e.pageSize;       // actualizamos la propiedad para el paginador
    this.load();
  }

  private load() {
    this.loading = true;
    this.peopleSvc.list(this.query).subscribe({
      next: (page: PageDto<Person>) => {
        this.data    = page.items;
        this.total   = page.total;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
