import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

/**
 * IstDatePipe — converts backend UTC timestamps to IST (UTC+5:30)
 *
 * The backend sends LocalDateTime without timezone suffix e.g. "2026-05-01T01:41:00.123"
 * Angular's date pipe treats that as local time. We append 'Z' to force UTC parsing,
 * then display in IST (+0530).
 *
 * Usage: {{ value | istDate }}
 *        {{ value | istDate:'dd MMM yyyy' }}
 *        {{ value | istDate:'dd MMM yyyy, hh:mm a' }}
 */
@Pipe({ name: 'istDate' })
export class IstDatePipe implements PipeTransform {

  private datePipe = new DatePipe('en-IN');

  transform(value: string | Date | null | undefined, format = 'dd MMM yyyy, hh:mm a'): string {
    if (!value) return '—';

    let date: Date;

    if (typeof value === 'string') {
      // Append 'Z' if no timezone info present — tells JS to treat as UTC
      const normalized = value.endsWith('Z') || value.includes('+') ? value : value + 'Z';
      date = new Date(normalized);
    } else {
      date = value;
    }

    if (isNaN(date.getTime())) return '—';

    // '+0530' = IST offset
    return this.datePipe.transform(date, format, '+0530') ?? '—';
  }
}
