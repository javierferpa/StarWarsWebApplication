import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MatPaginator,
  MatPaginatorModule,
  PageEvent
} from '@angular/material/paginator';
import {
  MatSort,
  MatSortModule,
  Sort
} from '@angular/material/sort';
import {
  MatTableDataSource,
  MatTableModule
} from '@angular/material/table';

@Component({
  selector: 'sw-data-table',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule
  ],
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent<T> {
  @Input() displayedColumns: string[] = [];
  @Input() total = 0;

  @Output() pageChange = new EventEmitter<PageEvent>();
  @Output() sortChange = new EventEmitter<Sort>();

  dataSource = new MatTableDataSource<T>();

  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
  @ViewChild(MatSort,      { static: true }) sort!: MatSort;

  /** whenever the parent sets new data, update the dataSource and re‑attach paginator & sort */
  @Input() set data(value: T[]) {
    this.dataSource.data = value;
    if (this.paginator) this.dataSource.paginator = this.paginator;
    if (this.sort)      this.dataSource.sort      = this.sort;
  }

  onPage(event: PageEvent) {
    this.pageChange.emit(event);
  }

  onSort(event: Sort) {
    this.sortChange.emit(event);
  }
}
