import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ServerTableDataSourceService } from './server-table-data-source.service';

@Injectable({
  providedIn: 'root'
})
export class DownloadService {

  private readonly url = `${environment.apiUrl}/download`;

  constructor(private readonly http: HttpClient) { }

  dataSource = inject(ServerTableDataSourceService);

  downloadExcel(): Observable<any> {
    const params: any = {
      'searchText': this.dataSource.getSearchText()
    }

    // Append filter fields to params if they are defined
    Object.entries(this.dataSource.getFilter()).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        // Convert arrays to comma-separated strings
        params[key] = Array.isArray(value) ? value.join(',') : value;
      }
    });
    return this.http.get(`${this.url}/xls`, { params: params, responseType: 'blob' });
  }
}
