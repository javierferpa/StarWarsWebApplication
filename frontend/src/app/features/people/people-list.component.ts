import { Component, OnInit }        from '@angular/core';
import { CommonModule }             from '@angular/common';
import { MatTableModule }           from '@angular/material/table';
import { MatPaginatorModule }       from '@angular/material/paginator';
import { MatSortModule }            from '@angular/material/sort';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { SearchBarComponent }       from '../../shared/search-bar/search-bar.component';
import { PeopleService }            from '../../core/services/people.service';
import { Person }                   from '../../core/models/people.model';
import { PageDto }                  from '../../core/models/page.model';
import {DataTableComponent} from '../../shared/data-table/data-table.component';
import {MatCard} from '@angular/material/card';
import {MatToolbar} from '@angular/material/toolbar';

@Component({
  selector:    'sw-people-list',
  standalone:  true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    SearchBarComponent,
    DataTableComponent,
    MatCard,
    MatToolbar
  ],
  templateUrl: './people-list.component.html',
  styleUrls:   ['./people-list.component.scss']
})
export class PeopleListComponent implements OnInit {
  displayed = ['name','height','mass','gender','created'];
  data:      Person[] = [];
  total    = 0;
  loading  = false;

  query = {
    page:   0,
    size:   15,
    sort:   'name',
    dir:    'asc' as 'asc' | 'desc',
    search: ''
  };

  constructor(private peopleSvc: PeopleService) {}

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
    this.peopleSvc.list(this.query).subscribe({
      next:   (page:PageDto<Person>) => {
        this.data    = page.items;
        this.total   = page.total;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
