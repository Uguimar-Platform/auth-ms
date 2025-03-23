export interface RoleModel {
  id?: number;
  name: string;
  createdAt: Date;
  updatedAt?: Date | null;
  status: number;
  page?: number;
  limit?: number;
}
