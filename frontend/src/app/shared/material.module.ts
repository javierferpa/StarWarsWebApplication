import { NgModule } from '@angular/core';
import { MatTableModule }     from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule }      from '@angular/material/sort';
import { MatInputModule }     from '@angular/material/input';
import { MatIconModule }      from '@angular/material/icon';
import { MatButtonModule }    from '@angular/material/button';
import { MatSnackBarModule }  from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatToolbarModule }   from '@angular/material/toolbar';
import { MatCardModule }      from '@angular/material/card';

const mat = [
  MatTableModule, MatPaginatorModule, MatSortModule,
  MatInputModule, MatIconModule, MatButtonModule,
  MatSnackBarModule, MatProgressSpinnerModule,
  MatToolbarModule, MatCardModule
];

@NgModule({
  exports: mat
})
export class MaterialModule {}
