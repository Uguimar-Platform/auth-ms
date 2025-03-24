
export interface RoleModel {
  id?: number;
  name: string;
  slug: string;
  permissionsId: number[];
  createdAt: Date;
  updatedAt?: Date | null;
  status: number;
  page?: number;
  limit?: number;
}
