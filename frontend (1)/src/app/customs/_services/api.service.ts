import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {CommonResponse} from "../_models/Response";

@Injectable({
  providedIn: 'root'
})

export class ApiService {
  constructor(private http: HttpClient) {
  }
  public put<T extends CommonResponse>(body: any, url: string, param: any): Observable<T> {
    return this.http.put<T>(url + '/' + param, body).pipe(
      catchError(this.handleError),
    );
  }

  public getBasedOnParams<T extends CommonResponse>(url: string): Observable<T> {
    return this.http.get<T>(url).pipe(
      catchError(this.handleError),
    );
  }

  public get<T extends CommonResponse>(param: any, url: string): Observable<T> {
    return this.http.get<T>(url + '/' + param).pipe(
      catchError(this.handleError),
    );
  }

  public getFile(url: string): Observable<Blob> {
    return this.http.get(url, { responseType: 'blob' })
      .pipe(
        catchError(this.handleError),
      );
  }


  public getQuery<T extends CommonResponse>(url: string): Observable<T> {
    return this.http.get<T>(url).pipe(
      catchError(this.handleError),
    );
  }

  public delete<T extends CommonResponse>(param: any, url: string): Observable<T> {
    return this.http.delete<T>(url + '/' + param).pipe(
      catchError(this.handleError),
    );
  }

  public post<T extends CommonResponse>(body: any, url: string): Observable<T> {
    return this.http.post<T>(url, body).pipe(
      catchError(this.handleError)
    );
  }

  public postWithFile<T extends CommonResponse>(url: string, params: any, file: File): Observable<T> {
    const formData = new FormData();

    // Add parameters to FormData
    for (const key in params) {
      if (params.hasOwnProperty(key)) {
        formData.append(key, params[key]);
      }
    }

    // If a file is provided, append it to FormData
    if (file) {
      formData.append('file', file);
    }

    return this.http.post<T>(url, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  public postWithMultipleFiles<T extends CommonResponse>(url: string, params: any, files: File[]): Observable<T> {
    const formData = new FormData();

    // Add parameters to FormData
    for (const key in params) {
      if (params.hasOwnProperty(key)) {
        formData.append(key, params[key]);
      }
    }

    if (files.length > 0) {
      for (const file of files) {
        formData.append("files", file);
      }
    }

    return this.http.post<T>(url, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  public putWithFile<T extends CommonResponse>(url: string, params: any, file: File): Observable<T> {
    const formData = new FormData();

    // Add parameters to FormData
    for (const key in params) {
      if (params.hasOwnProperty(key)) {
        formData.append(key, params[key]);
      }
    }

    // If a file is provided, append it to FormData
    if (file) {
      formData.append('file', file);
    }

    return this.http.put<T>(url, formData)
      .pipe(
        catchError(this.handleError)
      );
  }


  private handleError<T>(error: any): Observable<never> {
    console.error('An error occurred:', error);
    // Rethrow the error as an Observable of never type
    return throwError(error);
  }
}
