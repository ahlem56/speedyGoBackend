-- First, drop the existing foreign key constraint
ALTER TABLE commission 
    DROP FOREIGN KEY IF EXISTS FKpged5h3crcmdcr9yb40q5nuo5;

-- Remove redundant partner_partner_id column
ALTER TABLE commission DROP COLUMN IF EXISTS partner_partner_id;

-- Ensure proper foreign key constraints
ALTER TABLE commission 
    ADD CONSTRAINT fk_commission_partner 
    FOREIGN KEY (partner_id) 
    REFERENCES partners(partner_id);

ALTER TABLE commission 
    ADD CONSTRAINT fk_commission_payment 
    FOREIGN KEY (payment_payment_id) 
    REFERENCES payment(payment_id);

-- Create indexes for better performance
CREATE INDEX idx_commission_partner_id ON commission(partner_id);
CREATE INDEX idx_commission_payment_id ON commission(payment_payment_id); 