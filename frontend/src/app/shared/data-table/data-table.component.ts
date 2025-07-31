/* src/app/shared/data-table/data-table.component.ts */
import {
  AfterViewInit, Component, EventEmitter,
  Input, OnChanges, Output, SimpleChanges,
  ViewChild
} from '@angular/core';
import { CommonModule }           from '@angular/common';
import { MatTableDataSource }     from '@angular/material/table';
import {
  MatPaginator, MatPaginatorModule,
  PageEvent
} from '@angular/material/paginator';
import {
  MatSort, MatSortModule,
  Sort
} from '@angular/material/sort';
import { MatTableModule }         from '@angular/material/table';

@Component({
  selector   : 'sw-data-table',
  standalone : true,
  imports    : [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule
  ],
  templateUrl: './data-table.component.html',
  styleUrls  : ['./data-table.component.scss']
})
export class DataTableComponent<T>
  implements AfterViewInit, OnChanges {

  /** Columns to display */
  @Input() displayedColumns: string[] = [];
  /** Columns that can be sorted */
  @Input() sortableColumns: string[] = [];
  /** Data for the current page */
  @Input() data: T[] = [];
  /** Total number of records on the server */
  @Input() total = 0;
  /** Page size */
  @Input() pageSize = 15;

  /** Event emitter for page changes */
  @Output() pageChange = new EventEmitter<PageEvent>();
  /** Event emitter for sort changes */
  @Output() sortChange = new EventEmitter<Sort>();

  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
  @ViewChild(MatSort,      { static: true }) sort!     : MatSort;

  readonly dataSource = new MatTableDataSource<T>();

  /**
   * Checks if a column is sortable.
   * @param column The name of the column to check.
   * @returns True if the column is sortable, false otherwise.
   */
  isSortable(column: string): boolean {
    // If no sortable columns are specified, all are considered sortable by default.
    if (!this.sortableColumns || this.sortableColumns.length === 0) {
      return true;
    }
    return this.sortableColumns.includes(column);
  }

  ngAfterViewInit(): void {
    // We handle pagination and sorting on the server side,
    // so we don't assign the paginator or sort to the dataSource directly.
    // We only need to synchronize the paginator's label.
    this._syncPaginator();
    
    // Configure the sort to allow clearing (3-state cycle: asc -> desc -> no sort)
    if (this.sort) {
      this.sort.disableClear = false;
    }
  }

  ngOnChanges(ch: SimpleChanges): void {
    // If the data changes, refresh the table data
    if (ch['data']) {
      this.dataSource.data = this.data ?? [];
    }
    // If total or pageSize change, update the paginator
    if ((ch['total'] || ch['pageSize']) && this.paginator) {
      this._syncPaginator();
    }
  }

  private _syncPaginator() {
    this.paginator.length   = this.total;
    this.paginator.pageSize = this.pageSize;
    // Force a redraw of the "1-15 of 82" label
    this.paginator._intl?.changes.next();
  }

  onPage(e: PageEvent) { this.pageChange.emit(e); }
  onSort(e: Sort) { 
    // Emit the sort event even if it's cleared (active = '', direction = '')
    this.sortChange.emit(e); 
  }

  /**
   * Gets the display value for a cell, handling null/undefined values appropriately.
   * @param value The raw value from the data
   * @param columnName The name of the column (for context-specific formatting)
   * @returns The formatted display value
   */
  getDisplayValue(value: any, columnName: string): string {
    // Handle null, undefined, or empty string values
    if (value === null || value === undefined || value === '') {
      // For population, height, and mass columns, show "Unknown"
      if (columnName === 'population' || columnName === 'height' || columnName === 'mass') {
        return 'Unknown';
      }
      // For other columns, show a dash to indicate no data
      return '—';
    }
    
    // Handle "unknown" string values from the API
    if (typeof value === 'string' && value.toLowerCase() === 'unknown') {
      return 'Unknown';
    }
    
    // Return the value as string (including numeric zero which is a valid value)
    return String(value);
  }
}
