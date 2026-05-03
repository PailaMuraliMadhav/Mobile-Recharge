export interface AdminAnalytics {
  operatorUsage: { [key: string]: number };
  popularPlans: { [key: string]: number };
  revenueByDay: { [key: string]: number };
  paymentModeDistribution: { [key: string]: number };
  totalRecharges: number;
  totalRevenue: number;
}
