/**
 * The model of the module entity.
 */

export interface ModuleModel {
  id?: number;
  name: string;
  createdAt: Date;
  updatedAt?: Date;
  status: number;
}
