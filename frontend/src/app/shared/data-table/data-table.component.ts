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

  /** Columnas a pintar */
  @Input() displayedColumns: string[] = [];
  /** Datos de la página actual */
  @Input() data: T[] = [];
  /** Total de registros en el servidor (para el label) */
  @Input() total = 0;
  /** Tamaño de página */
  @Input() pageSize = 15;

  /** Emitir evento al cambiar página */
  @Output() pageChange = new EventEmitter<PageEvent>();
  /** Emitir evento al cambiar orden */
  @Output() sortChange = new EventEmitter<Sort>();

  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;
  @ViewChild(MatSort,      { static: true }) sort!     : MatSort;

  readonly dataSource = new MatTableDataSource<T>();

  ngAfterViewInit(): void {
    // No asignar paginador ni sort al dataSource para paginación de servidor
    // Solo sincronizar el label del paginador
    this._syncPaginator();
  }

  ngOnChanges(ch: SimpleChanges): void {
    // si cambian los datos, refrescarlos
    if (ch['data']) {
      this.dataSource.data = this.data ?? [];
    }
    // si total o pageSize cambian, actualizamos el paginador
    if ((ch['total'] || ch['pageSize']) && this.paginator) {
      this._syncPaginator();
    }
  }

  private _syncPaginator() {
    this.paginator.length   = this.total;
    this.paginator.pageSize = this.pageSize;
    // fuerza el redraw del label "1-15 of 82"
    this.paginator._intl?.changes.next();
  }

  onPage(e: PageEvent) { this.pageChange.emit(e); }
  onSort(e: Sort)      { this.sortChange.emit(e); }
}
