export interface PermissionsModel {
  id?: number;
  name: string;
  slug: string;
  moduleId: number;
  status: number;
  page?: number;
  limit?: number;
  createdAt?: Date;
  updatedAt?: Date | null;
}
