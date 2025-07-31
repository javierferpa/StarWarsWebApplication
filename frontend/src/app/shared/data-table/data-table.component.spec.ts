import { TestBed } from '@angular/core/testing';
import { DataTableComponent } from './data-table.component';

describe('DataTableComponent', () => {
  let component: DataTableComponent<any>;
  let fixture: any;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataTableComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(DataTableComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default properties', () => {
    expect(component.data).toEqual([]);
    expect(component.displayedColumns).toEqual([]);
    expect(component.total).toBe(0);
    expect(component.pageSize).toBe(15);
  });
});
